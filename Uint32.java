public class Uint32 {
    public static final int SIZE = 32;
    public static final long MAX = (1 << 32) - 1;
    public static final long MIN = 0L;
    public long value;

    public void set(long v) throws IllegalArgumentException {
        if (v < MIN || v > MAX) {
            throw new IllegalArgumentException(value + "值必须在" + MIN + "到" + MAX + "之间");
        }
        this.value = v;
    }

    public long get() {
        return this.value;
    }

    Uint32(long number) {
        this.value = number;
    }

    Uint32() {
        this(0L);
    }

    public byte[] getByteArray() {
        byte[] res = new byte[4];
        long t = value;
        for (int i = 3; i >= 0; i--) {
            res[i] = (byte) (t % 256);
            t >>= 8;
        }
        return res;
    }

    public static void main(String[] args) {
        Uint32 u1 = new Uint32(1);
        byte[] arr = u1.getByteArray();
    }
}
