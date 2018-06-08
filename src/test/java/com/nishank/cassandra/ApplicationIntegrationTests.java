package com.nishank.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.nishank.cassandra.config.CassandraConfig;
import com.nishank.cassandra.entity.Employee;
import com.nishank.cassandra.repository.EmployeeRepository;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CassandraConfig.class)
public class ApplicationIntegrationTests {

    public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS demoKeySpace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

    public static final String KEYSPACE_ACTIVATE_QUERY = "USE demoKeySpace;";

    public static final String DATA_TABLE_NAME = "employee";

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CassandraAdminOperations cassandraAdminOperations;

    @BeforeClass
    public static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();
        session.execute(KEYSPACE_CREATION_QUERY);
        session.execute(KEYSPACE_ACTIVATE_QUERY);
    }

    @AfterClass
    public static void stopCassandraEmbedded() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    public void createTable() {
        cassandraAdminOperations.createTable(true, CqlIdentifier.cqlId(DATA_TABLE_NAME), Employee.class, new HashMap());
    }

    @After
    public void dropeTable() {
        cassandraAdminOperations.dropTable(CqlIdentifier.cqlId(DATA_TABLE_NAME));
    }

    @Test
    public void insertion() {
        Employee employee = getEmployee();
        employeeRepository.save(employee);
        Employee result = employeeRepository.findByFname("Nishank");

        Assert.assertEquals(employee.getId(), result.getId());

    }

    @Test
    public void updation() {
        Employee employee = getEmployee();
        employeeRepository.save(employee);

        Employee result = employeeRepository.findByFname("Nishank");
        result.getCity().add("Delhi");
        employeeRepository.save(result);

        result = employeeRepository.findByFname("Nishank");

        Assert.assertEquals(result.getCity().size(), 3);

    }

    @Test
    public void deletion() {
        Employee employee = getEmployee();
        employeeRepository.save(employee);

        Employee result = employeeRepository.findByFname("Nishank");
        Assert.assertNotNull(result);

        employeeRepository.delete(result);
        result = employeeRepository.findByFname("Nishank");
        Assert.assertNull(result);
    }


    private Employee getEmployee() {
        Employee employee = new Employee();
        employee.setId(UUIDs.timeBased());
        employee.setfName("Nishank");
        employee.setlName("Gupta");

        Set<String> cities = new HashSet<>();
        cities.add("Bangalore");
        cities.add("Jamshedpur");
        employee.setCity(cities);
        return employee;
    }

}
