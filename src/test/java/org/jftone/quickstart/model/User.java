/**
 * User.java
 * User实体映射对象
 * 
 * @author		zhoubing
 * @date   		2019-05-21 10:05:07
 * @revision	v1.0
 */
package org.jftone.quickstart.model;

import javax.persistence.Column;
import javax.persistence.Table;
import org.jftone.model.Model;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;

@Entity
@Table(name="user")
public class User extends Model {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;

	@Column(name="passwd")
	private String passwd;

	@Column(name="email")
	private String email;

	@Column(name="address")
	private String address;

	@Column(name="user_no")
	private String userNo;

	@Column(name="province_no")
	private String provinceNo;

	@Column(name="city_no")
	private String cityNo;

	@Column(name="user_name")
	private String userName;

	@Column(name="county_no")
	private String countyNo;

	@Column(name="locked")
	private Short locked;

	@Column(name="create_time")
	private Long createTime;


	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getProvinceNo() {
		return provinceNo;
	}

	public void setProvinceNo(String provinceNo) {
		this.provinceNo = provinceNo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getCityNo() {
		return cityNo;
	}

	public void setCityNo(String cityNo) {
		this.cityNo = cityNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCountyNo() {
		return countyNo;
	}

	public void setCountyNo(String countyNo) {
		this.countyNo = countyNo;
	}

	public Short getLocked() {
		return locked;
	}

	public void setLocked(Short locked) {
		this.locked = locked;
	}

}
