/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.coldp;

import io.github.dhobern.coldp.TreeRenderProperties.ContextType;
import io.github.dhobern.coldp.TreeRenderProperties.TreeRenderType;
import static io.github.dhobern.utils.StringUtils.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author stang
 */
public class COLDPName implements Comparable<COLDPName>, TreeRenderable {
    
    private static final Logger LOG = LoggerFactory.getLogger(COLDPName.class);

    private String ID;
    private String basionymID;
    private String scientificName;
    private String authorship;
    private String rank;
    private String uninomial;
    private String genus;
    private String specificEpithet;
    private String infraspecificEpithet;
    private String referenceID;
    private String publishedInPage;
    private String publishedInYear;
    private String code;
    private String status;
    private String remarks;
    private String link;
    
    private RankEnum rankEnum = RankEnum.none;
    
    private COLDPName basionym;
    private Set<COLDPName> combinations;
    private COLDPReference reference;
    private List<COLDPNameReference> nameReferences;
    private List<COLDPNameRelation> nameRelations;
    private List<COLDPNameRelation> relatedNameRelations;
    private COLDPTaxon taxon;
    private List<COLDPSynonym> synonyms;

    public COLDPName() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBasionymID() {
        return (basionym == null) ? (basionymID == null ? ID : basionymID) : basionym.getID();
    }

    public void setBasionymID(String basionymID) {
        if (basionym == null) {
            this.basionymID = basionymID;
        } else {
            LOG.error("Attempted to set basionymID to " + basionymID + " when name associated with basionym " + basionym);
        }
        this.basionymID = basionymID;
    }

    public COLDPName getBasionym() {
        return basionym;
    }

    public void setBasionym(COLDPName basionym) {
        if (this.basionym != null && !this.basionym.equals(this)) {
            this.basionym.deregisterCombination(this);
        }
        this.basionym = basionym;
        basionymID = null;
        if (basionym != null && !basionym.equals(this)) {
            basionym.registerCombination(this);
        }
    }

    public Set<COLDPName> getCombinations() {
        return combinations;
    }

    void registerCombination(COLDPName combination) {
        if (combination != null) {
            if (combinations == null) {
                combinations = new HashSet<>();
            }
            combinations.add(combination);
        }
    }
 
    void deregisterCombination(COLDPName combination) {
        if (combination != null && combinations != null) {
            combinations.remove(combination);
        }
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getAuthorship() {
        return authorship;
    }

    public void setAuthorship(String authorship) {
        this.authorship = authorship;
    }

    public String getRank() {
        return rank;
    }
    
    public void setRank(String rank) {
        this.rank = rank;
    }

    public RankEnum getRankEnum() {
        if (rankEnum == RankEnum.none && rank != null) {
            try {
                String r = rank.toLowerCase();
                if (r.equals("class")) {
                    r = "clazz";
                }
                rankEnum = RankEnum.valueOf(r.toLowerCase());
            } catch (Exception e) {
                LOG.error("Could not parse rank name " + rank);
                rankEnum = RankEnum.unknown;
            }
        }
        
        return rankEnum;
    }

    public String getUninomial() {
        return uninomial;
    }

    public void setUninomial(String uninomial) {
        this.uninomial = uninomial;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public void fixGenus(String newGenus) {
        RankEnum currentRank = getRankEnum();
        if (currentRank.inSpeciesGroup() && !Objects.equals(genus, newGenus)) {
            genus = newGenus;
            scientificName = genus + " " + specificEpithet
                    + (currentRank.infraspecific()
                        ? " " + currentRank.getInfraspecificMarker() + " " + infraspecificEpithet
                    : "");
        }
    }
    
    public String getSpecificEpithet() {
        return specificEpithet;
    }

    public void setSpecificEpithet(String specificEpithet) {
        this.specificEpithet = specificEpithet;
    }

    public String getInfraspecificEpithet() {
        return infraspecificEpithet;
    }

    public void setInfraspecificEpithet(String infraspecificEpithet) {
        this.infraspecificEpithet = infraspecificEpithet;
    }

    public String getReferenceID() {
        return reference == null ? referenceID : reference.getID();
    }

    public void setReferenceID(String referenceID) {
        if (reference == null) {
            this.referenceID = referenceID;
        } else {
            LOG.error("Attempted to set referenceID to " + referenceID + " when name associated with reference " + reference);
        }
        this.referenceID = referenceID;
    }

    public COLDPReference getReference() {
        return reference;
    }

    public void setReference(COLDPReference reference) {
        if (this.reference != null) {
            this.reference.deregisterName(this);
        }
        this.reference = reference;
        referenceID = null;
        if (reference != null) {
            reference.registerName(this);
        }
    }

    public String getPublishedInPage() {
        return publishedInPage;
    }

    public void setPublishedInPage(String publishedInPage) {
        this.publishedInPage = publishedInPage;
    }

    public String getPublishedInYear() {
        return publishedInYear;
    }

    public void setPublishedInYear(String publishedInYear) {
        this.publishedInYear = publishedInYear;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<COLDPNameReference> getNameReferences() {
        return nameReferences;
    }

    void registerNameReference(COLDPNameReference nameReference) {
        if (nameReference != null) {
            if (nameReferences == null) {
                nameReferences = new ArrayList<>();
            }
            nameReferences.add(nameReference);
        }
    }
 
    void deregisterNameReference(COLDPNameReference nameReference) {
        if (nameReference != null && nameReferences != null) {
            nameReferences.remove(nameReference);
        }
    }
 
    public List<COLDPNameRelation> getNameRelations() {
        return nameRelations;
    }

    void registerNameRelation(COLDPNameRelation nameRelation) {
        if (nameRelation != null) {
            if (nameRelations == null) {
                nameRelations = new ArrayList<>();
            }
            nameRelations.add(nameRelation);
        }
    }
 
    void deregisterNameRelation(COLDPNameRelation nameRelation) {
        if (nameRelation != null && nameRelations != null) {
            nameRelations.remove(nameRelation);
        }
    }
 
    public List<COLDPNameRelation> getRelatedNameRelations() {
        return relatedNameRelations;
    }

    void registerRelatedNameRelation(COLDPNameRelation nameRelation) {
        if (nameRelation != null) {
            if (relatedNameRelations == null) {
                relatedNameRelations = new ArrayList<>();
            }
            relatedNameRelations.add(nameRelation);
        }
    }
 
    void deregisterRelatedNameRelation(COLDPNameRelation nameRelation) {
        if (nameRelation != null && relatedNameRelations != null) {
            relatedNameRelations.remove(nameRelation);
        }
    }

    public COLDPTaxon getTaxon() {
        return taxon;
    }

    public void setTaxon(COLDPTaxon taxon) {
        if (this.taxon != null) {
            LOG.error("Attempted to set taxon to " + taxon + " when name associated with taxon " + this.taxon);
        } else {
            this.taxon = taxon;
        }
    }

    public List<COLDPSynonym> getSynonyms() {
        return synonyms;
    }

    void registerSynonym(COLDPSynonym synonym) {
        if (synonym != null) {
            if (synonyms == null) {
                synonyms = new ArrayList<>();
            }
            synonyms.add(synonym);
        }
    }
 
    void deregisterSynonym(COLDPSynonym synonym) {
        if (synonym != null && synonyms != null) {
            synonyms.remove(synonym);
        }
    }
    
    public COLDPNameReference getRedundantNameReference(boolean allFields) {
        if (reference != null && nameReferences != null) {
            for (COLDPNameReference nr : nameReferences) {
                if (!nr.getReference().equals(reference)) continue;
                
                if (publishedInPage != null) {
                    if (nr.getPage() == null || !nr.getPage().equals(publishedInPage)) continue;
                } else if (nr.getPage() != null) continue;
                
                if (allFields) {
                    if (link != null) {
                        if (nr.getLink() == null || !nr.getLink().equals(link)) continue;
                    } else if (nr.getLink() != null) continue;

                    if (remarks != null) {
                        if (nr.getRemarks() == null || !nr.getRemarks().equals(remarks)) continue;
                    } else if (nr.getRemarks() != null) continue;
                }
                
                return nr;
            }
        }
        return null;
    }
    
    public String getNameStem() {
        if (rankEnum.inSpeciesGroup()) {
            String stem = genus + " " + trimToStem(specificEpithet);
            if (rankEnum.infraspecific()) {
                stem += " " + trimToStem(infraspecificEpithet);
            }
            return stem;
        }
        return scientificName;
    }
    
    public static String trimScientificNameToStem(String scientificName) {
       String[] words = scientificName.split(" +");
        if (words.length >= 2) {
            scientificName = words[0] + " " + trimToStem(words[1]);
            for (int i = 2; i < words.length - 1; i++) {
                if(!words[i].endsWith(".")) {
                    scientificName += " " + trimToStem(words[i]);
                }
            }
        }
        return scientificName;
    }
    
    public static String getScientificNameFromParts(RankEnum r, String uOrG, String s, String i) {
        String sn = uOrG;
        if (r.inSpeciesGroup()) {
            sn += " " + s;
            if (r.infraspecific()) {
                String marker = r.getInfraspecificMarker();
                if (marker != null && marker.length() > 0) {
                    sn += " " + marker;
                }
                sn += " " + i;
            }
        }
        return sn;
    }
    
    private static String trimToStem(String epithet) {
        if (epithet == null) {
            return "";
        } else if (epithet.endsWith("us")) {
            return epithet.substring(0, epithet.length() - 2);
        } else if (epithet.endsWith("a")) {
            return epithet.substring(0, epithet.length() - 1);
        }
        return epithet;
    }
    
    @Override
    public String toString() {
        return "CoLDP_Name{" + "ID=" + ID + ", basionymID=" + getBasionymID() + ", scientificName=" + scientificName + ", authorship=" + authorship + ", rank=" + rank + ", uninomial=" + uninomial + ", genus=" + genus + ", specificEpithet=" + specificEpithet + ", infraspecificEpithet=" + infraspecificEpithet + ", referenceID=" + getReferenceID() + ", publishedInPage=" + publishedInPage + ", publishedInYear=" + publishedInYear + ", code=" + code + ", status=" + status + ", remarks=" + remarks + ", link=" + link + '}';
    }
    
    public String toReferenceString() {
        return ID + " " + scientificName + " " + authorship;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.ID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final COLDPName other = (COLDPName) obj;
        if (!Objects.equals(this.ID, other.ID)) {
            return false;
        }
        return true;
    }
    
    public static String getCsvHeader() {
        return "ID,basionymID,scientificName,authorship,rank,uninomial,genus,"
                + "specificEpithet,infraspecificEpithet,referenceID,"
                + "publishedInPage,publishedInYear,code,status,remarks,link"; 
    }
    
    public String toCSV() {
        return buildCSV(ID, getBasionymID(),
                        scientificName, authorship, rank, uninomial, genus,
                        specificEpithet, infraspecificEpithet,
                        getReferenceID(), publishedInPage,
                        publishedInYear, code, status, remarks, link);
    }

    @Override
    public void render(PrintWriter writer, TreeRenderProperties context) {
        COLDPTaxon taxon = context.getCurrentTaxon();
        COLDPSynonym synonym = context.getCurrentSynonym();
        String nameRemarks = safeTrim(linkURLs(remarks));
        
        if (context.getTreeRenderType() == TreeRenderType.HTML) {
            String qualifier = "";
            if (synonym != null) {
                qualifier = "=&nbsp;";
            }

            String synonymRemarks = null;
            if (synonym != null) {
                synonymRemarks = safeTrim(linkURLs(synonym.getRemarks()));
                if(synonymRemarks == null && !synonym.getStatus().equalsIgnoreCase("synonym")) {
                    synonymRemarks = upperFirst(synonym.getStatus());
                }
            }
            if (synonymRemarks != null) {
                synonymRemarks = " (" + synonymRemarks + ")"; 
            } else {
                synonymRemarks = "";
            }
            
            if (reference != null) {
                context.addReference(reference);
            }

            String formatted = qualifier + upperFirst(getRank()) + synonymRemarks + ": " + wrapStrong(formatName(this));

            String indent = context.getIndent();
            writer.println(indent + "<div class=\"Name\">" + formatted);

            if (nameReferences != null) {
                for (COLDPNameReference nameReference : nameReferences) {
                    if (nameReference.getReference() == null) {
                        LOG.error("Reference missing for NameReference: " + nameReference.toString());
                    }
                    nameReference.render(writer, new TreeRenderProperties(context, this, ContextType.Name));
                }
            }

            if (nameRelations != null) {
                nameRelations.stream().sorted(Comparator.comparing(COLDPNameRelation::getSortString))
                        .forEach(nameRelation ->  {
                                    if (nameRelation.getReference() != null) {
                                        context.addReference(nameRelation.getReference());
                                    }
                                    nameRelation.render(writer, new TreeRenderProperties(context, this, ContextType.Name));
                                });
            }

            if (relatedNameRelations != null) {
                relatedNameRelations.stream().sorted(Comparator.comparing(COLDPNameRelation::getSortString))
                        .forEach(relatedNameRelation ->  {
                                    if (relatedNameRelation.getReference() != null) {
                                        context.addReference(relatedNameRelation.getReference());
                                    }
                                    relatedNameRelation.render(writer, new TreeRenderProperties(context, this, ContextType.Name));
                                });
            }

            if (synonym == null && remarks != null) {
                renderNote(writer, new TreeRenderProperties(context, this, ContextType.Name));
            }

            writer.println(indent + "</div>");
        }
    }
    
    private void renderNote(PrintWriter writer, TreeRenderProperties context) {
        if (context.getTreeRenderType() == TreeRenderProperties.TreeRenderType.HTML) {
            writer.println(context.getIndent() + wrapDiv("Note", "Note: " + wrapEmphasis(linkURLs(remarks))));
        }
    }    

    public static String formatName(COLDPName name) {
        String scientificName = name.getScientificName();
        switch (name.getRank()) {
            case "genus":
            case "species":
            case "subspecies":
                scientificName = wrapEmphasis(scientificName);
                break;
            case "variety":
                scientificName = wrapEmphasis(name.getGenus() + " " + name.getSpecificEpithet()) 
                        + " var. " + wrapEmphasis(name.getInfraspecificEpithet());
                break;
            case "form":
                scientificName = wrapEmphasis(name.getGenus() + " " + name.getSpecificEpithet()) 
                        + " f. " + wrapEmphasis(name.getInfraspecificEpithet());
                break;
            case "aberration":
                scientificName = wrapEmphasis(name.getGenus() + " " + name.getSpecificEpithet()) 
                        + " ab. " + wrapEmphasis(name.getInfraspecificEpithet());
                break;
        }
        
        String authorship = name.getAuthorship();
        if (authorship == null) {
            authorship = "";
        } else {
            authorship = " " + authorship;
        }

        return scientificName + authorship;    
    }

    @Override
    public int compareTo(COLDPName o) {
        return this.getID().compareTo(o.getID());
    }
}
