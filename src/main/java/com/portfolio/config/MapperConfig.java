
package com.portfolio.config;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;
@Configuration
public class MapperConfig{
@Bean public ModelMapper mapper(){return new ModelMapper();}
}
