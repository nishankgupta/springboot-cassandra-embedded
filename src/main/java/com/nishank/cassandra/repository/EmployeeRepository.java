package com.nishank.cassandra.repository;

import com.nishank.cassandra.entity.Employee;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Nishank Gupta on 08-Jun-18.
 */
@Repository
public interface EmployeeRepository extends CassandraRepository<Employee> {

    @Query("select * from employee where fName= ?0")
    Employee findByFname(String fName);
}
