package danna.ToDoList.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import danna.ToDoList.model.TagEntity;


@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    // JpaRepository ya te da métodos como findAll(), findById(), save(), deleteById()...
    List<TagEntity> findByUserId(Long userId);

    //List<TagEntity> findBySharedWith_Id(Long userId);
}