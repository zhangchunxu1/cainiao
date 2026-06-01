package com.example.employee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.employee.mapper")
public class EmployeeApplication {

    public static void main(String[] args) {
        freePort(8080);
        SpringApplication.run(EmployeeApplication.class, args);
    }

    private static void freePort(int port) {
        try {
            ProcessBuilder pb = new ProcessBuilder("powershell", "-Command",
                "(Get-NetTCPConnection -LocalPort " + port + " -ErrorAction SilentlyContinue).OwningProcess | ForEach-Object { taskkill /F /PID $_ }");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println(">>> Port " + port + " freed successfully");
            }
        } catch (Exception e) {
            System.out.println(">>> Port " + port + " check skipped: " + e.getMessage());
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 
