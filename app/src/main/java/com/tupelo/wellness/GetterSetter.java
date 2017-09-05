package com.tupelo.wellness;

/**
 * Created by Abhishek Singh Arya on 20-09-2015.
 */
public class GetterSetter {
    private static GetterSetter getterSetter;
    private String today_steps ="0", yesterday_steps ="0", best_single ="0", total_steps ="0";
    private String rank ="0";
    private String steps ="0";
    private String challenge_start ="";
    private String challenge_end ="";
    private String userid ="";
    private String username ="";
    private String corpid ="";
    private String sessionid ="";
    private String l_name ="";
    private String l_rank ="0";
    private String l_stepsLabel = "STEPS";
    private String l_steps ="0";
    private String l_challenge_start ="";
    private String l_challenge_end ="";
    private String l_challenge_unit ="";
    private String l_message ="";
    private String message ="";

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public void setL_stepLabel(String l_stepLabel)
    {
        this.l_stepsLabel = l_stepLabel;
    }
    public String getL_stepLabel()
    {
        return l_stepsLabel;
    }

    public String getL_rank() {
        return l_rank;
    }

    public void setL_rank(String l_rank) {
        this.l_rank = l_rank;
    }

    public String getL_steps() {
        return l_steps;
    }

    public void setL_steps(String l_steps) {
        this.l_steps = l_steps;
    }

    public String getL_challenge_start() {
        return l_challenge_start;
    }

    public void setL_challenge_start(String l_challenge_start) {
        this.l_challenge_start = l_challenge_start;
    }

    public String getL_challenge_end() {
        return l_challenge_end;
    }

    public void setL_challenge_end(String l_challenge_end) {
        this.l_challenge_end = l_challenge_end;
    }

    public String getL_challenge_unit() {
        return l_challenge_unit;
    }

    public void setL_challenge_unit(String l_challenge_unit) {
        this.l_challenge_unit = l_challenge_unit;
    }

    public String getL_message() {
        return l_message;
    }

    public void setL_message(String l_message) {
        this.l_message = l_message;
    }


    public static GetterSetter getInstance() {
        if (getterSetter == null) {
            getterSetter = new GetterSetter();
        }
        return getterSetter;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getChallenge_start() {
        return challenge_start;
    }

    public void setChallenge_start(String challenge_start) {
        this.challenge_start = challenge_start;
    }

    public String getChallenge_end() {
        return challenge_end;
    }

    public void setChallenge_end(String challenge_end) {
        this.challenge_end = challenge_end;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }


    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }


    public String getToday_steps() {
        return today_steps;
    }

    public void setToday_steps(String today_steps) {
        this.today_steps = today_steps;
    }

    public String getYesterday_steps() {
        return yesterday_steps;
    }

    public void setYesterday_steps(String yesterday_steps) {
        this.yesterday_steps = yesterday_steps;
    }

    public String getBest_single() {
        return best_single;
    }

    public void setBest_single(String best_single) {
        this.best_single = best_single;
    }

    public String getTotal_steps() {
        return total_steps;
    }

    public void setTotal_steps(String total_steps) {
        this.total_steps = total_steps;
    }


}
