package com.jgkj.bxxc.bean;

/**
 * Created by fangzhou on 2016/12/1.
 * 学员信息   一个实体类
 */

public class UserInfo {
    private int code;
    private String reason;
    private Result result;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public Result getResult() {
        return result;
    }

    public class Result{
        private int uid;
        private String phone;
        private String pic;
        private String state;
        private String sex;
        private String introduce;
        private String token;
        private String name;
        private String paypwd;
        private String useraccount;

        public String getClasstype() {
            return classtype;
        }

        public void setClasstype(String classtype) {
            this.classtype = classtype;
        }

        private String classtype;

        public String getPaypwd() {
            return paypwd;
        }

        public void setPaypwd(String paypwd) {
            this.paypwd = paypwd;
        }

        public String getAccount() {
            return useraccount;
        }

        public String getToken() {
            return token;
        }

        public String getPhone() {
            return phone;
        }

        public String getState() {
            return state;
        }


        public String getPic() {
            return pic;
        }


        public String getIntroduce() {
            return introduce;
        }


        public String getSex() {
            return sex;
        }


        public int getUid() {
            return uid;
        }


        public String getName() {
            return name;
        }


    }

}
