# Cảm ơn vì đã đọc
Cảm ơn vì đã để ý đến lời cảm ơn này.
# Phiên bản
Maven 3.9.11  
Java 17.0.12  
MySql 8.0.43  
# Trước khi chạy
Hãy đảm bảo rằng đã tạo database và chỉnh sửa file src/main/resources/application.properties sao cho đúng với database đang sử dụng  
Code tạo database ở file teaching schedule.sql

# Cách khởi động
Chạy các lệnh sau:  
mvn clean install  
mvn spring-boot:run

# Chạy docker
docker-compose up --build -d

# Push docker
docker push tyr24/teaching-schedule-api-app:latest
docker pull tyr24/teaching-schedule-api-app:latest