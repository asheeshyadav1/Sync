package com.cmpt213.finalProject.SYNC.repository;

import com.cmpt213.finalProject.SYNC.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepo extends JpaRepository<UserModel, Integer> {
    
    @Query("SELECT uf.friend FROM UserFriend uf WHERE uf.user.id = :userId")
    List<UserModel> findAllFriendsByUserId(@Param("userId") Integer userId);
}