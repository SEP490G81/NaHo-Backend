package org.naho.shared.specification;

public final class SpecificationHelper {
    private SpecificationHelper() {
    }

    /**
     * Thay các kí tự đặc biệt trọng SQL:
     * 1. \ => \\
     * 2. % => \%
     * 3. _ => \_
     *
     * @param s chuỗi cần thay thế
     * @return chuỗi mới sau khi đã thay thế
     */
    public static String escapeLikePattern(String s) {
        return s.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
