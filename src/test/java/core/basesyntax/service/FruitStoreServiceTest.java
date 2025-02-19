package core.basesyntax.service;

import static org.junit.Assert.assertEquals;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.db.Storage;
import core.basesyntax.model.Fruit;
import core.basesyntax.model.TransactionDto;
import core.basesyntax.service.impl.FruitStoreServiceImpl;
import core.basesyntax.startegy.ActivityHandler;
import core.basesyntax.startegy.ActivityStrategy;
import core.basesyntax.startegy.ActivityType;
import core.basesyntax.startegy.impl.ActivityStrategyImpl;
import core.basesyntax.startegy.impl.AddActivityHandler;
import core.basesyntax.startegy.impl.BalanceActivityHandler;
import core.basesyntax.startegy.impl.PurchaseActivityHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FruitStoreServiceTest {
    private static FruitDao fruitDao;
    private static final Map<ActivityType, ActivityHandler> ACTIVITY_HANDLER_MAP = new HashMap<>();
    private static ActivityStrategy activityStrategy;
    private static List<TransactionDto> transactions;
    private static FruitStoreService fruit;

    @BeforeClass
    public static void beforeClass() {
        fruitDao = new FruitDaoImpl();
        activityStrategy = new ActivityStrategyImpl(ACTIVITY_HANDLER_MAP);
        transactions = new ArrayList<>();
        fruit = new FruitStoreServiceImpl(fruitDao, activityStrategy);
        ACTIVITY_HANDLER_MAP.put(ActivityType.BALANCE, new BalanceActivityHandler(fruitDao));
        ACTIVITY_HANDLER_MAP.put(ActivityType.PURCHASE, new PurchaseActivityHandler(fruitDao));
        ACTIVITY_HANDLER_MAP.put(ActivityType.RETURN, new AddActivityHandler(fruitDao));
        ACTIVITY_HANDLER_MAP.put(ActivityType.SUPPLY, new AddActivityHandler(fruitDao));
    }
    
    @Before
    public void beforeEachTest() {
        transactions.add(new TransactionDto("b", "banana", 65));
        transactions.add(new TransactionDto("s", "banana", 5));
        transactions.add(new TransactionDto("r", "banana", 5));
        transactions.add(new TransactionDto("p", "banana", 10));
    }

    @Test
    public void changeBalance_allActivity_ok() {
        List<Fruit> expected = new ArrayList<>();
        expected.add(new Fruit("banana", 65));
        List<Fruit> actual = fruit.changeBalance(transactions);
        assertEquals(expected, actual);
    }

    @Test (expected = RuntimeException.class)
    public void changeBalance_purchaseMoreThanBalance_notOk() {
        transactions.add(new TransactionDto("p", "banana", 75));
        fruit.changeBalance(transactions);
    }

    @After
    public void afterEachTest() {
        Storage.fruits.clear();
        transactions.clear();
    }
}
