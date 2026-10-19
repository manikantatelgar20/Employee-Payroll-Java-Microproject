public class PayrollWorker implements Runnable {

    private String taskName;

    public PayrollWorker(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println(
                taskName + " started on "
                + Thread.currentThread().getName());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Task interrupted.");
            return;
        }

        System.out.println(taskName + " completed.");
    }
}