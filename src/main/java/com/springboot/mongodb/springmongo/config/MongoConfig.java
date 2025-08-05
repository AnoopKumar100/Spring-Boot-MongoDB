

//This class is used for removing _class field from MongoDB documents,  commented now

/*package com.springboot.mongodb.springmongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;



@Configuration
public class MongoConfig {

    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory mongoDatabaseFactory,
            MongoMappingContext mongoMappingContext,
            MongoCustomConversions mongoCustomConversions) {

        MappingMongoConverter converter = new MappingMongoConverter(mongoDatabaseFactory, mongoMappingContext);
        converter.setCustomConversions(mongoCustomConversions);
        // This will remove the _class field from MongoDB documents
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}

*/

