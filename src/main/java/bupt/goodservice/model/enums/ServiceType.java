package bupt.goodservice.model.enums;


/**
 * 服务类型枚举
 * 定义各种社区服务、养老服务等类型
 */
public enum ServiceType {

    /**
     * 管道维修服务
     */
    PIPELINE_MAINTENANCE("管道维修"),

    /**
     * 助老服务
     */
    ELDERLY_ASSISTANCE("助老服务"),

    /**
     * 保洁服务
     */
    CLEANING_SERVICE("保洁服务"),

    /**
     * 就诊服务
     */
    MEDICAL_APPOINTMENT("就诊服务"),

    /**
     * 营养餐服务
     */
    NUTRITIONAL_MEALS("营养餐服务"),

    /**
     * 定期接送服务
     */
    REGULAR_TRANSPORTATION("定期接送服务"),
    /**
     * 其它服务
     */
    OTHER("其它服务");

    private final String chineseName;

    /**
     * 构造函数
     *
     * @param chineseName 中文名称
     */
    ServiceType(String chineseName) {
        this.chineseName = chineseName;
    }

    /**
     * 根据中文名称获取枚举
     *
     * @param chineseName 中文名称
     * @return 对应的枚举，如果找不到返回null
     */
    public static ServiceType getByChineseName(String chineseName) {
        for (ServiceType type : values()) {
            if (type.getChineseName().equals(chineseName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查中文名称是否存在
     *
     * @param chineseName 中文名称
     * @return 是否存在
     */
    public static boolean containsChineseName(String chineseName) {
        return getByChineseName(chineseName) != null;
    }

    /**
     * 获取所有服务的中文名称数组
     *
     * @return 中文名称数组
     */
    public static String[] getAllChineseNames() {
        ServiceType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getChineseName();
        }
        return names;
    }

    /**
     * 获取中文名称
     *
     * @return 中文名称
     */
    public String getChineseName() {
        return chineseName;
    }

    @Override
    public String toString() {
        return chineseName;
    }
}
