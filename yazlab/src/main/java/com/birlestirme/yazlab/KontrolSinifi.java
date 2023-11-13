package com.birlestirme.yazlab;


import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

import java.io.IOException;


@Controller
public class KontrolSinifi {
    private static final String FILE_NAME = "metinler.json";
    
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/submit-form")
    public String birlestir(@RequestParam List<String> metinler, Model model) {
//Süre hesaplama yapılıyor.

    long startTime = System.nanoTime();
    long endTime = System.nanoTime();

    long elapsedTime = endTime - startTime;

    
    try {//metinler.json dosyasına kaydetme işlemi
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(FILE_NAME), metinler);
    } catch (JsonGenerationException e) {
        e.printStackTrace();
    } catch (JsonMappingException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    model.addAttribute("sure", elapsedTime);


        StringBuilder birlesmisMetinBuilder = new StringBuilder(); // birleştirilen metinler burada tutulacak
        String ortakKelime = "";
    
        // ilk metni birleştir
        String[] ilkKelimeler = metinler.get(0).split(" ");
        for (int i = 0; i < ilkKelimeler.length; i++) {
            birlesmisMetinBuilder.append(ilkKelimeler[i]).append(" ");
            if (i == ilkKelimeler.length - 1) {
                ortakKelime = ilkKelimeler[i];
            }
        }
        // diğer metinlerle karşılaştır ve birleştir
        for (int i = 1; i < metinler.size(); i++) {
            String suankiMetin = metinler.get(i); // şu anki metin
            String[] suankiKelimeler = suankiMetin.split(" "); // kelimeleri diziye ayır
    
            int ortakKelimeIndex = -1;
            for (int j = 0; j < suankiKelimeler.length; j++) {
                if (suankiKelimeler[j].equals(ortakKelime)) {
                    ortakKelimeIndex = j;
                    break;
                }
            }
    
            StringBuilder birlesmisMetinBuilder2 = new StringBuilder(birlesmisMetinBuilder);
            if (ortakKelimeIndex >= 0) {
                // şu anki metnin ortak kelimesinden sonra gelen kelimeleri birleştir
                for (int j = ortakKelimeIndex + 1; j < suankiKelimeler.length; j++) {
                    birlesmisMetinBuilder2.append(suankiKelimeler[j]).append(" ");
                }
            } else {
                // şu anki metni tamamen birleştir
                birlesmisMetinBuilder2.append(suankiMetin).append(" ");
            }
    
            ortakKelime = suankiKelimeler[suankiKelimeler.length - 1];
            birlesmisMetinBuilder = birlesmisMetinBuilder2;
        }
        
        // birleşmiş metni model ile görünüme gönder
        model.addAttribute("birlesmisMetin", birlesmisMetinBuilder.toString().trim());


        String birlesmisMetin = birlesmisMetinBuilder.toString().trim(); // birleştirilmiş metni al
    
    // metinler listesine birleştirilmiş metni ekle
    metinler.add(birlesmisMetin);
    
    // JSON dosyasına metinler listesini yazdır
    try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(FILE_NAME), metinler);
    } catch (JsonGenerationException e) {
        e.printStackTrace();
    } catch (JsonMappingException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
        return "index";
    }



    
    
}