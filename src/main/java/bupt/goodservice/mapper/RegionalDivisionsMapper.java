package bupt.goodservice.mapper;

import bupt.goodservice.model.RegionalDivision;

import java.util.List;

/**
 * @author 86157
 * @description 针对表【regional_divisions(中国行政区划表-市级)】的数据库操作Mapper
 * @createDate 2025-09-28 20:29:06
 * @Entity bupt.goodservice.model.RegionalDivision
 */
public interface RegionalDivisionsMapper {
    List<Long> selectAll();

    List<RegionalDivision> selectAllEntity();

    int deleteByPrimaryKey(Long id);

    int insert(RegionalDivision record);

    int insertSelective(RegionalDivision record);

    RegionalDivision selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RegionalDivision record);

    int updateByPrimaryKey(RegionalDivision record);
}
