package bupt.goodservice.mapper;

import bupt.goodservice.model.ServiceRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceRequestMapper {
    List<Long> selectAll();
    void insert(ServiceRequest serviceRequest);
    ServiceRequest findById(Long id);
    ServiceRequest findByIdForUpdate(Long id);
    List<ServiceRequest> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    List<ServiceRequest> findAll(@Param("serviceType") String serviceType, @Param("regionId") Long regionId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    void update(ServiceRequest serviceRequest);
    void delete(Long id);
}
