package com.kassaiweb.ibiza.Poll;

import java.util.ArrayList;

public class Poll {

    private String id;
    private String question;
    private String choice;
    private ArrayList<Answer> answers = new ArrayList<>();
    private boolean closed;
    private boolean publicResult;
    private String creatorId;

    public Poll() {
    }

    public Poll(String id, String question, String choice, ArrayList<Answer> answers, boolean publicResult, String creatorId) {
        this.id = id;
        this.question = question;
        this.choice = choice;
        this.answers = answers;
        this.publicResult = publicResult;
        this.creatorId = creatorId;
        this.closed=false;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public int getVotesNumber() {
        int votesNumber=0;
        for (Answer answer : answers)
        {
            for (Boolean vote : answer.getVotes().values())
            {
                if (vote) { votesNumber++; }
            }
        }

        return votesNumber;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isPublicResult() {
        return publicResult;
    }

    public void setPublicResult(boolean publicResult) {
        this.publicResult = publicResult;
    }
}
