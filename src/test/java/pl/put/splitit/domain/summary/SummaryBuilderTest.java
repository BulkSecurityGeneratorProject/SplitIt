package pl.put.splitit.domain.summary;

import org.junit.Before;
import org.junit.Test;
import pl.put.splitit.domain.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krystek on 2016-11-05.
 */
public class SummaryBuilderTest {

    List<Transaction> transactions = new ArrayList<>();

    @Before
    public void init(){
        System.out.println("init");
    }

    @Test
    public void buildUserSummary() throws Exception {

    }

}
