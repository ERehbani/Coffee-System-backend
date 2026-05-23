package com.CoffeeGroup.Coffee.System.common.model;


public interface IMapper <T,U>{
    T toEntity(U u);
    U toDto(T t);
}
