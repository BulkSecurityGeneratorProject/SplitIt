package pl.put.splitit.domain.summary;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import pl.put.splitit.SplitItApp;
import pl.put.splitit.domain.Transaction;
import pl.put.splitit.domain.User;
import pl.put.splitit.domain.UserGroup;
import pl.put.splitit.web.rest.TransactionResourceIntTest;
import pl.put.splitit.web.rest.UserGroupResourceIntTest;
import pl.put.splitit.web.rest.UserResourceIntTest;

import javax.persistence.EntityManager;

/**
 * Created by Krystek on 2016-11-05.
 */
@SpringBootTest(classes = SplitItApp.class)
public class SummaryBuilderTest {
    private static final double DELTA = 1E-7;
    @Mock
    private static EntityManager em;

    static User admin, test, user;
    static UserGroup first, second;
    static Transaction t0, t1, t2, t3, t4, t5, t6, t7, t8;

    @BeforeClass
    public static void setup() {
        em = Mockito.mock(EntityManager.class);

        // Add users
        admin = UserResourceIntTest.createEntity(em, "admin");
        test = UserResourceIntTest.createEntity(em, "test");
        user = UserResourceIntTest.createEntity(em, "user");

        // Add groups
        first = UserGroupResourceIntTest.createEntity(em, "First Group");
        second = UserGroupResourceIntTest.createEntity(em, "Second Group");

        t0 = TransactionResourceIntTest.createEntity(em, admin, user, -10D, first); // User buys sth for admin for 10$
        t1 = TransactionResourceIntTest.createEntity(em, user, admin, -6D, first); // Admin buys sth for user for 6$
        t2 = TransactionResourceIntTest.createEntity(em, admin, user, 5D, first); // Admin returns 5$ to user
        t3 = TransactionResourceIntTest.createEntity(em, user, admin, 2D, first); // User returns 2$ to admin
        t4 = TransactionResourceIntTest.createEntity(em, admin, test, 5D, first); // Admin returns 5$ to user

        t5 = TransactionResourceIntTest.createEntity(em, admin, user, -10D, second); // User buys sth for admin for 10$
        t6 = TransactionResourceIntTest.createEntity(em, user, admin, -6D, second); // Admin buys sth for user for 6$
        t7 = TransactionResourceIntTest.createEntity(em, admin, user, 5D, second); // Admin returns 5$ to user
        t8 = TransactionResourceIntTest.createEntity(em, user, admin, 2D, second); // User returns 2$ to admin
    }

    //<editor-fold desc="User summary">
    @Test
    public void userSummary_SingleTransactionInOneGroup() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0);
        UserSummary summary = builder.buildUserSummary(admin, user);
        Assert.assertEquals(-10.0, summary.getTotal(), DELTA);
    }


    @Test
    public void userSummary_SingleTransactionInOneGroup_TestDebtValue() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0);
        UserSummary summary = builder.buildUserSummary(admin, user);
        Assert.assertEquals(10.0, summary.getTotalAsDebitor(), DELTA);
    }

    @Test
    public void userSummary_SingleTransactionInOneGroup_TestCreditValue() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0);
        UserSummary summary = builder.buildUserSummary(admin, user);
        Assert.assertEquals(0.0, summary.getTotalAsCreditor(), DELTA);
    }

    @Test
    public void userSummary_SingleTransactionInOneGroup_TestDebtValue_FromUsersPointOfView() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0);
        UserSummary summary = builder.buildUserSummary(user, admin);
        Assert.assertEquals(0.0, summary.getTotalAsDebitor(), DELTA);
    }

    @Test
    public void userSummary_SingleTransactionInOneGroup_TestCreditValue_FromUsersPointOfView() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0);
        UserSummary summary = builder.buildUserSummary(user, admin);
        Assert.assertEquals(10.0, summary.getTotalAsCreditor(), DELTA);
    }

    @Test
    public void userSummary_DoubleTransactionInOneGroup() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t0);
        UserSummary summary = builder.buildUserSummary(admin, user);

        Assert.assertEquals(-20.0, summary.getTotal(), DELTA);
    }


    @Test
    public void userSummary_SingleDebtAndCreditInOneGroup() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t1);
        UserSummary summary = builder.buildUserSummary(admin, user);

        Assert.assertEquals(-4.0, summary.getTotal(), DELTA);
    }

    @Test
    public void userSummary_SingleDebtAndCreditWithTwoUsersInOneGroup() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t1, t4); // t4 should not be counted
        UserSummary summary = builder.buildUserSummary(admin, user);

        Assert.assertEquals(-4.0, summary.getTotal(), DELTA);
    }

    @Test
    public void userSummary_DoubleDebtAndDoubleReturnInOneGroup() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t1, t2, t3);
        UserSummary summary = builder.buildUserSummary(admin, user);

        Assert.assertEquals(-1.0, summary.getTotal(), DELTA);
    }

    @Test
    public void userSummary_DoubleDebtAndDoubleReturnInOneGroup_FromUsersPointOfView() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t1, t2, t3);
        UserSummary summary = builder.buildUserSummary(user, admin);

        Assert.assertEquals(1.0, summary.getTotal(), DELTA);
    }

    @Test
    public void userSummary_DoubleDebtAndDoubleReturnInTwoGroups() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t1, t2, t3, t4, t5, t6, t7, t8);
        UserSummary summary = builder.buildUserSummary(admin, user);

        Assert.assertEquals(-2.0, summary.getTotal(), DELTA);
    }

    @Test
    public void userSummary_DoubleDebtAndDoubleReturnInTwoGroups_FromUsersPointOfView() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t1, t2, t3, t4, t5, t6, t7, t8);
        UserSummary summary = builder.buildUserSummary(user, admin);

        Assert.assertEquals(2.0, summary.getTotal(), DELTA);
    }

    @Test
    public void userSummary_DoubleDebtAndDoubleReturnInOneOfTwoGroups() throws Exception {
        SummaryBuilder builder = new SummaryBuilder(t0, t1, t2, t3, t4, t5, t6, t7, t8);
        UserSummary summary = builder.buildUserSummary(first, admin, user);

        Assert.assertEquals(-1.0, summary.getTotal(), DELTA);
    }

    //</editor-fold>



}
