package org.example;

import java.util.function.Consumer;
import java.util.function.Function;

public class ApiHandler {

    // Sửa kiểu trả về thành R1 (hoặc Object nếu muốn linh hoạt)
    // vì bạn muốn lấy kết quả từ API
    public <P, R1, R2> R1 callApi(
            Consumer<P> step1,
            Consumer<P> step2,
            Function<P, R1> execReturn1, // Giả sử dùng chung bộ Params P
            Function<P, R2> execReturn2,
            P params,
            String msgAlert,
            Consumer<String> showAlert,
            Runnable execFunc
    ) {
        try {
            // 1. Thực thi các bước đệm
            step1.accept(params);
            step2.accept(params);

            // 2. Thực thi các hàm API
            R1 response1 = execReturn1.apply(params);
            R2 response2 = execReturn2.apply(params); // Chạy nhưng không return cái này

            // 3. Logic thông báo và callback
            if (showAlert != null && msgAlert != null) {
                showAlert.accept(msgAlert);
            }
            if (execFunc != null) {
                execFunc.run();
            }

            // 4. Trả về kết quả cuối cùng (Phải để ở cuối hàm)
            return response1;

        } catch (Exception e) {
            if (showAlert != null) showAlert.accept("Error: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        ApiHandler apiHandler = new ApiHandler();

        // Gọi hàm với đầy đủ tham số đã khai báo
        String finalResult = apiHandler.callApi(
                data -> step1(),
                data -> System.out.println("Step 2 thực thi với: " + data),
                data -> {
                    System.out.println("API 1 run...");
                    return "Result From API 1";
                },
                data -> {
                    System.out.println("API 2 run...");
                    return 100; // Trả về số nguyên (R2)
                },
                "My Params",
                "Thành công!",
                msg -> System.out.println("ALERT: " + msg),
                () -> System.out.println("Finished!")
        );

        System.out.println("Kết quả cuối cùng: " + finalResult);
    }

    public static void step1(){
        System.out.println("Step 1 is running...");
    }
}