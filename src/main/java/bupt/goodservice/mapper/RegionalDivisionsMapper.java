package bupt.goodservice.mapper;

import bupt.goodservice.model.RegionalDivisions;

import java.util.List;

/**
 * @author 86157
 * @description 针对表【regional_divisions(中国行政区划表-市级)】的数据库操作Mapper
 * @createDate 2025-09-28 20:29:06
 * @Entity bupt.goodservice.model.RegionalDivisions
 */
public interface RegionalDivisionsMapper {
    List<Long> selectAll();

    int deleteByPrimaryKey(Long id);

    int insert(RegionalDivisions record);

    int insertSelective(RegionalDivisions record);

    RegionalDivisions selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RegionalDivisions record);

    int updateByPrimaryKey(RegionalDivisions record);

}
