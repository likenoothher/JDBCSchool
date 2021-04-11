package com.foxminded.sqlschool.schoolrepository;

public class DaoFactory {

    public GenericDao getDao(DaoType type) {
        if (type == DaoType.COURSE_DAO) {
            return new CourseDao();
        }
        if (type == DaoType.STUDENT_DAO) {
            return new StudentDao();
        }
        return new GroupDao();

    }
}
