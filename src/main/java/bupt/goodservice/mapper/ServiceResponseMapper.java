package bupt.goodservice.mapper;

import bupt.goodservice.model.ServiceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ServiceResponseMapper {
    void insert(ServiceResponse serviceResponse);
    ServiceResponse findById(Long id);
    List<ServiceResponse> findByRequestId(@Param("requestId") Long requestId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    List<ServiceResponse> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    List<ServiceResponse> findAll(@Param("offset") int offset, @Param("pageSize") int pageSize);
    void update(ServiceResponse serviceResponse);
    void delete(Long id);
}
