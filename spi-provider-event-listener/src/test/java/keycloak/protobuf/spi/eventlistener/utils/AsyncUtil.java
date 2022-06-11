package keycloak.protobuf.spi.eventlistener.utils;

import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.fail;

public class AsyncUtil {

    protected AsyncUtil() {}

    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private static ConcurrentHashMap<UUID, AssertionError> errorMap = new ConcurrentHashMap<>();

    public static void eventually(Runnable r) {
        eventually(r, 10000);
    }

    public static void eventually(Runnable r, long timeout) {
        eventually(r, timeout, 100);
    }

    public static void eventually(Runnable r, long timeout, long interval) {
        UUID execution = UUID.randomUUID();
        long end = System.currentTimeMillis() + timeout;
        long tries = 0;
        long failed = 0;
        boolean success = false;
        AssertionError lastError = null;
        while (System.currentTimeMillis() < end) {
            tries++;
            Future<Void> f = executor.submit(new Invocation(r, execution));
            try {
                f.get(timeout, TimeUnit.MILLISECONDS);
                if (errorMap.containsKey(execution)) {
                    throw errorMap.get(execution);
                }
                success = true;
                break;
            } catch (InterruptedException ex) {
                failed++;
                try { Thread.sleep(interval); } catch (InterruptedException ex2) {}
            } catch (ExecutionException ex) {
                failed++;
                try { Thread.sleep(interval); } catch (InterruptedException ex2) {}
            } catch (TimeoutException ex) {
                failed++;
                try { Thread.sleep(interval); } catch (InterruptedException ex2) {}
            } catch (AssertionError ex) {
                failed++;
                lastError = ex;
                try { Thread.sleep(interval); } catch (InterruptedException ex2) {}
            } finally {
                errorMap.remove(execution);
            }
        }
        if (!success) {
            fail("Eventually executed " + tries + " times and failed " + failed + " times after " + timeout + " milliseconds. Underlying reason: " + lastError + ".");
        }
    }

    private static class Invocation implements Callable<Void> {
        private final Runnable task;
        private final UUID execution;
        public Invocation(Runnable r, UUID exec) {
            this.task = r;
            this.execution = exec;
        }
        public Void call() {
            InvocationUncaughtExceptionHandler handler = new InvocationUncaughtExceptionHandler();
            Thread t = new Thread(task);
            t.setUncaughtExceptionHandler(handler);
            t.start();
            try { t.join(); } catch (InterruptedException ex) {}
            if (handler.hadException()) {
                errorMap.put(execution, handler.getException());
            }
            return null;
        }
    }

    private static class InvocationUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private AssertionError ae;
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            if (AssertionError.class.isAssignableFrom(e.getClass())) {
                ae = (AssertionError) e;
            }
        }
        public boolean hadException() {
            return ae != null;
        }
        public AssertionError getException() {
            return ae;
        }
    }

}
