//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.01 at 10:15:08 AM CEST 
//


package com.dabi.habitv.provider.canalplus.initplayer.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}PUB"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pub"
})
@XmlRootElement(name = "RECHERCHE")
public class RECHERCHE {

    @XmlElement(name = "PUB", required = true)
    protected PUB pub;

    /**
     * Gets the value of the pub property.
     * 
     * @return
     *     possible object is
     *     {@link PUB }
     *     
     */
    public PUB getPUB() {
        return pub;
    }

    /**
     * Sets the value of the pub property.
     * 
     * @param value
     *     allowed object is
     *     {@link PUB }
     *     
     */
    public void setPUB(PUB value) {
        this.pub = value;
    }

}
