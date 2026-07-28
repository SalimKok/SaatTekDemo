package com.example.demo;

import com.example.demo.entity.Cast;
import com.example.demo.service.CastService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


@Bean
public CommandLineRunner testUygulamasi(CastService castService) {
	return args -> {
		System.out.println("--- UYGULAMA BAŞLADI: VERİTABANI TESTİ YAPILIYOR ---");

		// 1. Yeni bir oyuncu nesnesi oluşturuyoruz (Henüz ID'si yok)
		Cast yeniOyuncu = new Cast();
		yeniOyuncu.setName("Brad Pitt");
		yeniOyuncu.setPosterUrl("Brad_matrix.jpg");

		// 2. Service katmanımızı kullanarak veritabanına kaydediyoruz
		Cast kaydedilenOyuncu = castService.saveActor(yeniOyuncu);

		// 3. Veritabanının ona atadığı ID'yi konsola yazdırıyoruz (Return mantığının kanıtı)
		System.out.println("Başarıyla Kaydedildi! Veritabanındaki ID'si: " + kaydedilenOyuncu.getId());
		System.out.println("Oyuncu Adı: " + kaydedilenOyuncu.getName());

		System.out.println("--------------------------------------------------");
	};
}
}