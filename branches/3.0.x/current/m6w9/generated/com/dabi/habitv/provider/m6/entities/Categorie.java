//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.11 at 10:06:21 PM CEST 
//


package com.dabi.habitv.provider.m6.entities;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{}categorie"/>
 *           &lt;element ref="{}nom"/>
 *           &lt;element ref="{}Liens"/>
 *         &lt;/choice>
 *         &lt;element ref="{}produit" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="advertising_pack" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="big_img_url" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="hours_restriction" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="pub_chapitre" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="pub_midroll" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="pub_postroll" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="pub_preroll" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="reg_img_url" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="shares_vp" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="sml_img_url" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="tag_dart" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="tag_preroll" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="url_fb" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "categorieOrNomOrLiens",
    "produit"
})
@XmlRootElement(name = "categorie")
public class Categorie {

    @XmlElements({
        @XmlElement(name = "nom", type = String.class),
        @XmlElement(name = "Liens", type = Liens.class),
        @XmlElement(name = "categorie", type = Categorie.class)
    })
    protected List<Object> categorieOrNomOrLiens;
    protected List<Produit> produit;
    @XmlAttribute(name = "advertising_pack")
    @XmlSchemaType(name = "anySimpleType")
    protected String advertisingPack;
    @XmlAttribute(name = "big_img_url", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String bigImgUrl;
    @XmlAttribute(name = "hours_restriction")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String hoursRestriction;
    @XmlAttribute(required = true)
    protected BigInteger id;
    @XmlAttribute(name = "pub_chapitre", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String pubChapitre;
    @XmlAttribute(name = "pub_midroll", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String pubMidroll;
    @XmlAttribute(name = "pub_postroll", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String pubPostroll;
    @XmlAttribute(name = "pub_preroll", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String pubPreroll;
    @XmlAttribute(name = "reg_img_url", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String regImgUrl;
    @XmlAttribute(name = "shares_vp")
    @XmlSchemaType(name = "anySimpleType")
    protected String sharesVp;
    @XmlAttribute(name = "sml_img_url", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String smlImgUrl;
    @XmlAttribute(name = "tag_dart")
    @XmlSchemaType(name = "anySimpleType")
    protected String tagDart;
    @XmlAttribute(name = "tag_preroll")
    @XmlSchemaType(name = "anySimpleType")
    protected String tagPreroll;
    @XmlAttribute(name = "url_fb")
    @XmlSchemaType(name = "anyURI")
    protected String urlFb;

    /**
     * Gets the value of the categorieOrNomOrLiens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categorieOrNomOrLiens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategorieOrNomOrLiens().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * {@link Liens }
     * {@link Categorie }
     * 
     * 
     */
    public List<Object> getCategorieOrNomOrLiens() {
        if (categorieOrNomOrLiens == null) {
            categorieOrNomOrLiens = new ArrayList<Object>();
        }
        return this.categorieOrNomOrLiens;
    }

    /**
     * Gets the value of the produit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the produit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProduit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Produit }
     * 
     * 
     */
    public List<Produit> getProduit() {
        if (produit == null) {
            produit = new ArrayList<Produit>();
        }
        return this.produit;
    }

    /**
     * Gets the value of the advertisingPack property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdvertisingPack() {
        return advertisingPack;
    }

    /**
     * Sets the value of the advertisingPack property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdvertisingPack(String value) {
        this.advertisingPack = value;
    }

    /**
     * Gets the value of the bigImgUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBigImgUrl() {
        return bigImgUrl;
    }

    /**
     * Sets the value of the bigImgUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBigImgUrl(String value) {
        this.bigImgUrl = value;
    }

    /**
     * Gets the value of the hoursRestriction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoursRestriction() {
        return hoursRestriction;
    }

    /**
     * Sets the value of the hoursRestriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoursRestriction(String value) {
        this.hoursRestriction = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the pubChapitre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubChapitre() {
        return pubChapitre;
    }

    /**
     * Sets the value of the pubChapitre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubChapitre(String value) {
        this.pubChapitre = value;
    }

    /**
     * Gets the value of the pubMidroll property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubMidroll() {
        return pubMidroll;
    }

    /**
     * Sets the value of the pubMidroll property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubMidroll(String value) {
        this.pubMidroll = value;
    }

    /**
     * Gets the value of the pubPostroll property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubPostroll() {
        return pubPostroll;
    }

    /**
     * Sets the value of the pubPostroll property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubPostroll(String value) {
        this.pubPostroll = value;
    }

    /**
     * Gets the value of the pubPreroll property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubPreroll() {
        return pubPreroll;
    }

    /**
     * Sets the value of the pubPreroll property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubPreroll(String value) {
        this.pubPreroll = value;
    }

    /**
     * Gets the value of the regImgUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegImgUrl() {
        return regImgUrl;
    }

    /**
     * Sets the value of the regImgUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegImgUrl(String value) {
        this.regImgUrl = value;
    }

    /**
     * Gets the value of the sharesVp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSharesVp() {
        return sharesVp;
    }

    /**
     * Sets the value of the sharesVp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSharesVp(String value) {
        this.sharesVp = value;
    }

    /**
     * Gets the value of the smlImgUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmlImgUrl() {
        return smlImgUrl;
    }

    /**
     * Sets the value of the smlImgUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmlImgUrl(String value) {
        this.smlImgUrl = value;
    }

    /**
     * Gets the value of the tagDart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTagDart() {
        return tagDart;
    }

    /**
     * Sets the value of the tagDart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTagDart(String value) {
        this.tagDart = value;
    }

    /**
     * Gets the value of the tagPreroll property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTagPreroll() {
        return tagPreroll;
    }

    /**
     * Sets the value of the tagPreroll property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTagPreroll(String value) {
        this.tagPreroll = value;
    }

    /**
     * Gets the value of the urlFb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlFb() {
        return urlFb;
    }

    /**
     * Sets the value of the urlFb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlFb(String value) {
        this.urlFb = value;
    }

}
