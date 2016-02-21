package org.drack.hackathon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("test")
class DockerPostgresTestConfig {


    private static final String DOCKER_MACHINE_SERVICE_URL = "https://192.168.99.100:2376";

    private static final long DOCKER_CONTAINER_STARTUP_TIMEOUT = 60000L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerPostgresTestConfig.class);



    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.docker-image}")
    private String dockerImage;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.db}")
    private String database;

    @Value("${spring.datasource.db-port}")
    private int databasePort;

    private String containerId;
    private DockerClient dockerClient;

    @Bean
    public DataSource dataSource() throws Exception{

        dockerClient = createDockerClient();

        final ContainerConfig containerConfig = createContainerConfig(dockerImage,
                                                                      new String[]{String.valueOf(databasePort)},
                                                                      null,
                                                                      ImmutableList.of(
                                                                         "POSTGRES_DB=" + database,
                                                                         "POSTGRES_USER=" + user,
                                                                         "POSTGRES_PASSWORD=" + password));

        dockerClient.pull(dockerImage);
        final ContainerCreation container = dockerClient.createContainer(containerConfig);
        dockerClient.startContainer(container.id());

        containerId = container.id();

        waitForPort(dockerClient, databasePort, DOCKER_CONTAINER_STARTUP_TIMEOUT);

        return DataSourceBuilder
                .create()
                .driverClassName(driverClassName)
                .username(user)
                .password(password)
                .url(String.format("jdbc:postgresql://%s:%s/%s", dockerClient.getHost(), databasePort, database))
                .build();
    }


    @PreDestroy
    public void destroy() throws Exception {
        // TODO looks wrong but it is not critical because it is test code
        if(dockerClient != null && containerId != null) {
            LOGGER.debug("shutting down [dockerContainer={}]", containerId);
            dockerClient.killContainer(containerId);
        }
    }


    private void waitForPort(final DockerClient dockerClient, final int port, final long timeoutInMillis) {
        final SocketAddress address = new InetSocketAddress(dockerClient.getHost(), port);
        long totalWait = 0;
        while (true) {
            try {
                SocketChannel.open(address);
                return;
            } catch (IOException e) {
                try {
                    Thread.sleep(100);
                    totalWait += 100;
                    if (totalWait > timeoutInMillis) {
                        throw new IllegalStateException("Timeout while waiting for port " + port);
                    }
                } catch (final InterruptedException ie) {
                    throw new IllegalStateException(ie);
                }
            }
        }
    }


    private DockerClient createDockerClient() {
        if (isUnix() || System.getenv("DOCKER_HOST") != null) {
            try {
                return DefaultDockerClient.fromEnv().build();
            } catch (DockerCertificateException e) {
                System.err.println(e.getMessage());
            }
        }

        LOGGER.info("Could not create docker client from the environment. Assuming docker-machine environment with url " + DOCKER_MACHINE_SERVICE_URL);
        DockerCertificates dockerCertificates = null;
        try {
            String userHome = System.getProperty("user.home");
            dockerCertificates = new DockerCertificates(Paths.get(userHome, ".docker/machine/certs"));
        } catch (DockerCertificateException e) {
            System.err.println(e.getMessage());
        }
        return DefaultDockerClient.builder()
                .uri(URI.create(DOCKER_MACHINE_SERVICE_URL))
                .dockerCertificates(dockerCertificates)
                .build();
    }


    private static boolean isUnix() {
        final String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }


    private ContainerConfig createContainerConfig(String imageName, String[] ports, String cmd, List<String> env) {
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String port : ports) {
            portBindings.put(port, Lists.newArrayList(PortBinding.of("0.0.0.0", port)));
        }

        final HostConfig hostConfig = HostConfig.builder()
                .portBindings(portBindings)
                .build();

        ContainerConfig.Builder configBuilder = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(imageName)
                .networkDisabled(false)
                .exposedPorts(ports)
                .env(env)
                ;
        if (cmd != null) {
            configBuilder = configBuilder.cmd(cmd);
        }
        return configBuilder.build();
    }
}
