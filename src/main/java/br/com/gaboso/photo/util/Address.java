package br.com.gaboso.photo.util;

import br.com.gaboso.photo.module.ClubP;

import java.util.ArrayList;
import java.util.List;

public class Address {

    private List<String> addressList = new ArrayList<>();


    public static void main(String[] args) {
        Address address = new Address();

        address.addressList.add("clubninahartley");
        address.addressList.add("clubesperanzagomez");

        ClubP clubP = new ClubP();

        for (String item : address.addressList) {
            clubP.download(item, "C:\\photos\\");
        }

    }

}
