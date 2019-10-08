package config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configure {
    int[] portPool;
    String cypher;

    String ssServerIp;
    short ssPort;

    long closeBaseTime;
}
