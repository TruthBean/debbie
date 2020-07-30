package com.truthbean.debbie.httpclient.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 车票信息表
 */
public class Ticket implements Serializable {
  /**
   * 主键
   */
  private Long id;

  /**
   * 乘客姓名
   */
  private String name;

  /**
   * 乘客联系电话
   */
  private String phoneNumber;

  /**
   * 乘客身份证号
   */
  private String idNumber;

  /**
   * 列车ID
   */
  private Integer trainId;

  /**
   * 车厢Id
   */
  private Integer carriageId;

  /**
   * 出发站
   */
  private Integer leaveScheduleId;

  /**
   * 到达站
   */
  private Integer arriveScheduleId;

  /**
   * 座位Id
   */
  private Integer seatId;

  /**
   * 乘客人脸图片地址
   */
  private String image;

  private String base64Image;

  /**
   * 乘客人脸特征
   */
  private float[] feature;

  /**
   * 创建时间，时间戳
   */
  private Long createTime;

  /**
   * 最近修改时间，时间戳
   */
  private Long updateTime;

  public Ticket() {
  }

  public Ticket(Ticket ticket) {
    this.id = ticket.id;
    this.name = ticket.name;
    this.phoneNumber = ticket.phoneNumber;
    this.idNumber = ticket.idNumber;
    this.trainId = ticket.trainId;
    this.carriageId = ticket.carriageId;
    this.leaveScheduleId = ticket.leaveScheduleId;
    this.arriveScheduleId = ticket.arriveScheduleId;
    this.seatId = ticket.seatId;
    this.image = ticket.image;
    this.feature = ticket.feature;
    this.createTime = ticket.createTime;
    this.updateTime = ticket.updateTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getTrainId() {
    return trainId;
  }

  public void setTrainId(Integer trainId) {
    this.trainId = trainId;
  }

  public Integer getCarriageId() {
    return carriageId;
  }

  public void setCarriageId(Integer carriageId) {
    this.carriageId = carriageId;
  }

  public Integer getSeatId() {
    return seatId;
  }

  public void setSeatId(Integer seatId) {
    this.seatId = seatId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIdNumber() {
    return idNumber;
  }

  public void setIdNumber(String idNumber) {
    this.idNumber = idNumber;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getBase64Image() {
    return base64Image;
  }

  public void setBase64Image(String base64Image) {
    this.base64Image = base64Image;
  }

  public float[] getFeature() {
    return feature;
  }

  public void setFeature(float[] feature) {
    this.feature = feature;
  }

  public Integer getLeaveScheduleId() {
    return leaveScheduleId;
  }

  public void setLeaveScheduleId(Integer leaveScheduleId) {
    this.leaveScheduleId = leaveScheduleId;
  }

  public Integer getArriveScheduleId() {
    return arriveScheduleId;
  }

  public void setArriveScheduleId(Integer arriveScheduleId) {
    this.arriveScheduleId = arriveScheduleId;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public String toString() {
    return "{" +
        "id:" + id +
        ",name:'" + name + '\'' +
        ",phoneNumber:'" + phoneNumber + '\'' +
        ",idNumber:'" + idNumber + '\'' +
        ",trainId:" + trainId +
        ",carriageId:" + carriageId +
        ",leaveScheduleId:" + leaveScheduleId +
        ",arriveScheduleId:" + arriveScheduleId +
        ",seatId:" + seatId +
        ",image:'" + image + '\'' +
        ",base64Image:'" + base64Image + '\'' +
        ",feature:'" + Arrays.toString(feature) + '\'' +
        ",createTime:" + createTime +
        ",updateTime:" + updateTime +
        '}';
  }
}
