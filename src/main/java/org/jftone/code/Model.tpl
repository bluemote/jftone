/**
 * ${entity!}.java
 * ${entity!}实体映射对象
 * 
 * @author		zhoubing
 * @date   		${date!}
 * @revision	v1.0
 */
package ${package!};

<#if imports??>
<#list imports.keySet() as key>
import ${key!};
</#list>
</#if>

@Entity
@Table(name="${tableName!}")
public class ${entity!} extends Model {
<#list propertys as property>
<#if property.id?? &&  property.id== 'true'>
	@Id
	@GeneratedValue(strategy=${property.strategy!})
</#if>
	@Column(name="${property.fieldName!}"<#if property.columnDefinition??>, columnDefinition="${property.columnDefinition!}"</#if>)
	private ${property.type!} ${property.name!};

</#list>
<#list methods as obj>
	public ${obj.type!} get${obj.method!}() {
		return ${obj.field!};
	}

	public void set${obj.method!}(${obj.type!} ${obj.field!}) {
		this.${obj.field!} = ${obj.field!};
	}

</#list>
}
