package tech.mogami.spring.example;

public record WeatherResponse(Report report) {
    public record Report(String weather, int temperature) {
    }
}

