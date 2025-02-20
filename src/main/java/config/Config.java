package config;

public class Config {
    //Constants
    public static final int COST_FACTOR = 16384;
    public static final int BLOCK_SIZE = 8;
    public static final int PARALLELIZATION_USER = 1;
    public static final int PARALLELIZATION_KEY = 2;
    public static final int KEY_LENGTH = 32;
    public static final byte[] SIGNATURE_MARK = "###SIGNATURE###".getBytes();
    public static final byte[] TIME_MARK = "###DATE###".getBytes();
}
