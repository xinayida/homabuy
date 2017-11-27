package com.nhzw.shopping.model.entity;

import com.xinayida.lib.annotation.NotProguard;

/**
 * 描述: 地址实体.<br/><br/>
 * 作者：由姚宇编辑于2017/11/1.<br/><br/>
 */
@NotProguard
public class Contact {
    private String name = "";
    private String phone = "";
    private String address = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
