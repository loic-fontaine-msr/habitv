//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.27 at 07:56:31 AM CEST 
//


package com.dabi.habitv.provider.m6.entities;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}M6Replay_banner"/>
 *         &lt;element ref="{}categorie" maxOccurs="unbounded"/>
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
    "m6ReplayBanner",
    "categorie"
})
@XmlRootElement(name = "template_exchange_WEB")
public class TemplateExchangeWEB {

    @XmlElement(name = "M6Replay_banner", required = true)
    protected M6ReplayBanner m6ReplayBanner;
    @XmlElement(required = true)
    protected List<Categorie> categorie;

    /**
     * Gets the value of the m6ReplayBanner property.
     * 
     * @return
     *     possible object is
     *     {@link M6ReplayBanner }
     *     
     */
    public M6ReplayBanner getM6ReplayBanner() {
        return m6ReplayBanner;
    }

    /**
     * Sets the value of the m6ReplayBanner property.
     * 
     * @param value
     *     allowed object is
     *     {@link M6ReplayBanner }
     *     
     */
    public void setM6ReplayBanner(M6ReplayBanner value) {
        this.m6ReplayBanner = value;
    }

    /**
     * Gets the value of the categorie property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categorie property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategorie().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Categorie }
     * 
     * 
     */
    public List<Categorie> getCategorie() {
        if (categorie == null) {
            categorie = new ArrayList<Categorie>();
        }
        return this.categorie;
    }

}
