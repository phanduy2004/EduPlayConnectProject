package com.myjob.real_time_chat_final.model;

import java.util.List;

public class WordResult {
    private String word;
    private String phonetic;
    private List<Meaning> meanings;

    public WordResult(String word, String phonetic, List<Meaning> meanings) {
        this.word = word;
        this.phonetic = phonetic;
        this.meanings = meanings;
    }

    public String getWord() {
        return word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }
}
