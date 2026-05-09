package org.example;

import java.util.function.Consumer;

public class ApiHandler {

    public <P> void callApi(
            Consumer<P> step1,      // Đổi sang Consumer để không cần return
            Consumer<P> step2,
            P params,
            String msgAlert,
            Consumer<String> showAlert,
            Runnable execFunc
    ) {
        try {
            // Thực thi (không cần hứng biến R response)
            step1.accept(params);
            step2.accept(params);

            if (showAlert != null && msgAlert != null) {
                showAlert.accept(msgAlert);
            }
            if (execFunc != null) {
                execFunc.run();
            }
        } catch (Exception e) {
            if (showAlert != null) showAlert.accept("Error: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        ApiHandler apiHandler = new ApiHandler();

        apiHandler.callApi(
                data -> step1(), // Không cần return nữa vì là Consumer
                data -> System.out.println("Step 2 thực thi với: " + data),
                "My Params",
                "Thành công!",
                msg -> System.out.println("ALERT: " + msg),
                () -> System.out.println("Finished!")
        );
    }

    // Bỏ static nếu bạn gọi callApi từ một method non-static khác,
// nhưng giữ static nếu gọi trực tiếp từ main như trên.
    public static void step1(){
        System.out.println("Step 1 is running...");
    }
}