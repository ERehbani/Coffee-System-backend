package com.CoffeeGroup.Coffee.System.features.client.dto;




public record UpdateClientRequest (
        String name,

        String lastName,

        String phone,

        String profilePhoto
){
}
