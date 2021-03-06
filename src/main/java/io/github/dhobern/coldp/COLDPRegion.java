/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.coldp;

import io.github.dhobern.utils.StringUtils;
import static io.github.dhobern.utils.StringUtils.upperFirst;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author stang
 */
public class COLDPRegion implements TreeRenderable {
    
    private static final Logger LOG = LoggerFactory.getLogger(COLDPRegion.class);
      
    private String ID;
    private String name;
    
    private Set<COLDPDistribution> distributions;

    public COLDPRegion() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<COLDPDistribution> getDistributions() {
        return distributions;
    }

    void registerDistribution(COLDPDistribution distribution) {
        if (distribution != null) {
            if (distributions == null) {
                distributions = new HashSet<>();
            }
            distributions.add(distribution);
        }
    }
 
    void deregisterDistribution(COLDPDistribution distribution) {
        if (distribution != null && distributions != null) {
            distributions.remove(distribution);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.ID);
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
        final COLDPRegion other = (COLDPRegion) obj;
        if (!Objects.equals(this.ID, other.ID)) {
            return false;
        }
        return true;
    }
    
    public static String getCsvHeader() {
        return "ID,name"; 
    }
    
    public String toCsv() {
        return StringUtils.buildCSV(ID, name);
    }
    
    public String toString() {
        return  ID + " " + name;
    }

    @Override
    public void render(PrintWriter writer, TreeRenderProperties context) {
        TreeRenderProperties.TreeRenderType renderType = context.getTreeRenderType();
        TreeRenderProperties childContext = new TreeRenderProperties(context, this, TreeRenderProperties.ContextType.Region);

        writer.println(context.getIndent() + renderType.openNodeWithID("Region", "region-" + ID));
        writer.println(childContext.getIndent() + renderType.openNode("Name") + name + renderType.closeNode());

        if (distributions != null) {
            renderDistributions(writer,  childContext, renderType);
        }
        
        String closeNode = renderType.closeNode();
        if (closeNode.length() > 0) {
            writer.println(context.getIndent() + closeNode);
        }
    }
    
    private void renderDistributions(PrintWriter writer, TreeRenderProperties context, TreeRenderProperties.TreeRenderType renderType) {
        writer.println(context.getIndent() + renderType.openNode("Taxa"));

        Set<COLDPDistribution> sorted = new TreeSet<>(new COLDPDistribution.TaxonSort());
        sorted.addAll(distributions);
        for (COLDPDistribution distribution : sorted) {
            distribution.render(writer, new TreeRenderProperties(context, this, TreeRenderProperties.ContextType.Distribution));
        }
    }    
}
