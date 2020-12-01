### 1.快速开始

#### 1.1 @EnableModel

使用此注解开启模型服务适配

```java
@EnableModel
public class EemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EemServiceApplication.class, args);
    }

}

```

#### 1.2 @ModelLabel

使用此注解定义在模型映射的实体上，并让实体继承通用的模型实体基类AbstractModelEntity或者实现IModel接口

```java
@Getter
@Setter
@ModelLabel("energyconsumption")
public class EnergyConsumption extends AbstractModelEntity {

    /**
     * 聚合周期
     */
    @JsonProperty(EnergyConsumptionLabel.LabelColumn.AGGREGATION_CYCLE)
    private AggregationCycle aggregationCycle;

    /**
     * 时段结束时间
     */
    @JsonProperty(EnergyConsumptionLabel.LabelColumn.ENDTIME)
    private LocalDateTime endTime;

    /**
     * 能源类型
     */
    @JsonProperty(EnergyConsumptionLabel.LabelColumn.ENERGY_TYPE)
    private EnergyType energyType;

    /**
     * 数据记录时间
     */
    @JsonProperty(EnergyConsumptionLabel.LabelColumn.LOGTIME)
    private LocalDateTime logTime;

    /**
     * 损耗
     */
    private Double loss;

    /**
     * 用能对象ID
     */
    @JsonProperty(EnergyConsumptionLabel.LabelColumn.OBJECT_ID)
    private Long objectId;

    /**
     * 用能对象模型Label
     */
    @JsonProperty(EnergyConsumptionLabel.LabelColumn.OBJECT_LABEL)
    private String objectLabel;

    /**
     * 分时时段标识
     */
    @JsonProperty(EnergyConsumptionLabel.LabelColumn.TIMESHARE_PERIOD_IDENTIFICATION)
    private String timesharePeriodIdentification;

    /**
     * 总能耗
     */
    private Double total;

    /**
     * 用量
     */
    private Double usage;

  
}
```



#### 1.3 BaseModelDao

将数据访问层接口继承此接口，会自动扫描此接口，并生成代理类托管给spring

```java
public interface EnergyConsumptionDao extends BaseModelDao<EnergyConsumption>{
    
}
```

#### 1.4 ModelDaoImpl

当通用的数据访问无法满足特定需求时，可以继承此类，获取通用访问的功能，也可通过模型服务原生API操作数据库

```java
@Repository
public class EnergyConsumptionDaoImpl extends ModelDaoImpl<EnergyConsumption> implements EnergyConsumptionDao{
    
}
```

#### 1.5 服务配置

```yml
cet:
  eem:
    url:
      model-service: ''
```

#### 1.6 依赖导入

```xml
        <dependency>
            <groupId>com.cet</groupId>
            <artifactId>model-spring-boot-starter</artifactId>
            <version>0.0.3</version>
        </dependency>
```

