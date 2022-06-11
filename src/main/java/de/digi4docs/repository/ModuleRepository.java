package de.digi4docs.repository;

import de.digi4docs.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    List<Module> findAllByCourseIdOrderByOrderPositionAscTitleAsc(Integer courseId);

    List<Module> findAllByParentIdOrderByOrderPositionAscTitleAsc(Integer moduleId);

    Optional<Module> findFirstByParentIdOrderByOrderPositionDesc(Integer parentId);

    Optional<Module> findFirstByCourseIdOrderByOrderPositionDesc(Integer courseId);

    Optional<Module> findFirstByParentIdAndOrderPositionGreaterThanOrderByOrderPositionAsc(Integer parentId,
            Integer orderPosition);

    Optional<Module> findFirstByParentIdAndIsActiveTrueAndOrderPositionGreaterThanOrderByOrderPositionAsc(Integer parentId,
            Integer orderPosition);

    Optional<Module> findFirstByCourseIdAndOrderPositionGreaterThanOrderByOrderPositionAsc(Integer courseId,
            Integer orderPosition);

    Optional<Module> findFirstByCourseIdAndIsActiveTrueAndOrderPositionGreaterThanOrderByOrderPositionAsc(Integer courseId,
            Integer orderPosition);

    Optional<Module> findFirstByParentIdAndOrderPositionLessThanOrderByOrderPositionDesc(Integer parentId,
            Integer orderPosition);

    Optional<Module> findFirstByParentIdAndIsActiveTrueAndOrderPositionLessThanOrderByOrderPositionDesc(Integer parentId,
            Integer orderPosition);

    Optional<Module> findFirstByCourseIdAndOrderPositionLessThanOrderByOrderPositionDesc(Integer courseId,
            Integer orderPosition);

    Optional<Module> findFirstByCourseIdAndIsActiveTrueAndOrderPositionLessThanOrderByOrderPositionDesc(Integer courseId,
            Integer orderPosition);

    List<Module> findAllByAsBadgeTrueAndIsActiveTrueOrderByParentAscOrderPositionDesc();
}
