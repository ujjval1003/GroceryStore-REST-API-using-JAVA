/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ACER
 */
@Entity
@Table(name = "token_blacklist")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TokenBlacklist.findAll", query = "SELECT t FROM TokenBlacklist t"),
    @NamedQuery(name = "TokenBlacklist.findById", query = "SELECT t FROM TokenBlacklist t WHERE t.id = :id"),
    @NamedQuery(name = "TokenBlacklist.findByToken", query = "SELECT t FROM TokenBlacklist t WHERE t.token = :token"),
    @NamedQuery(name = "TokenBlacklist.findByBlacklistedAt", query = "SELECT t FROM TokenBlacklist t WHERE t.blacklistedAt = :blacklistedAt")})
public class TokenBlacklist implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "token")
    private String token;
    @Basic(optional = false)
    @NotNull
    @Column(name = "blacklisted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date blacklistedAt;

    public TokenBlacklist() {
    }

    public TokenBlacklist(Long id) {
        this.id = id;
    }

    public TokenBlacklist(Long id, String token, Date blacklistedAt) {
        this.id = id;
        this.token = token;
        this.blacklistedAt = blacklistedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getBlacklistedAt() {
        return blacklistedAt;
    }

    public void setBlacklistedAt(Date blacklistedAt) {
        this.blacklistedAt = blacklistedAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TokenBlacklist)) {
            return false;
        }
        TokenBlacklist other = (TokenBlacklist) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.TokenBlacklist[ id=" + id + " ]";
    }
    
}
