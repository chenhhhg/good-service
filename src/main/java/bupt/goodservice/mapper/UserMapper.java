package bupt.goodservice.mapper;

import bupt.goodservice.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {
    List<Long> selectAll();
    User findByUsername(String username);
    void insert(User user);
    void update(User user);
    List<User> findAll(@Param("offset") int offset, @Param("pageSize") int pageSize);

    User findById(Long userId);

    List<User> findBatchById(@Param("ids") Set<Long> ids);
}
