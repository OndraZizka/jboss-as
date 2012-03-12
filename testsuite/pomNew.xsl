<?xml version="2.0" encoding="UTF-8"?>
<!--
     Transforms new pom.xml format into the 4.0.0.
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:mvn="http://maven.apache.org/POM/4.0.0"
    >
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/project/parent[@gav]">
        <parent>
            <groupId><xsl:value-of select="substring-before(@gav, ':')"></groupId>
            <artifactId><xsl:value-of select="substring-before( substring-after(@gav, ':') , ':')"></artifactId>
            <version><xsl:value-of select="substring-after( substring-after(@gav, ':') , ':')"></version>
        </parent>
    </xsl:template>


    <!--             <dependency gav="org.jboss.arquillian:arquillian-bom:${version.arquillian_core}" scope="import" type="pom"/> -->
    <xsl:template match="/project/dependencyManagement/dependencies/dependency[@gav]">
        <dependency>
            <groupId><xsl:value-of select="substring-before(@gav, ':')"></groupId>
            <artifactId><xsl:value-of select="substring-before( substring-after(@gav, ':') , ':')"></artifactId>
            <version><xsl:value-of select="substring-after( substring-after(@gav, ':') , ':')"></version>
            <xsl:if test="@scope"><scope><xsl:value-of select="@scope"/></scope></xsl:if>
            <xsl:if test="@type"><type><xsl:value-of select="@type"/></type></xsl:if>
        </dependency>
    </xsl:template>

    <!--  <dependency gav="org.jboss.arquillian:arquillian-bom:${version.arquillian_core}" scope="import" type="pom"/> -->
    <xsl:template match="/project/dependencies/dependency[@gav]">
        <dependency>
            <groupId><xsl:value-of select="substring-before(@gav, ':')"></groupId>
            <artifactId><xsl:value-of select="substring-before( substring-after(@gav, ':') , ':')"></artifactId>
            <version><xsl:value-of select="substring-after( substring-after(@gav, ':') , ':')"></version>
            <xsl:if test="@scope"><scope><xsl:value-of select="@scope"/></scope></xsl:if>
            <xsl:if test="@type"><type><xsl:value-of select="@type"/></type></xsl:if>
        </dependency>
    </xsl:template>

    <!--  <parameter name="managementIPAddress" value="${node0}"/>   TODO: Copy potential other elements. -->
    <xsl:template match="//parameter[@name and @value]">
        <parameter><name><xsl:value-of select="@name"/></name> <value><xsl:value-of select="value"/></value></parameter>
    </xsl:template>


    <!-- Copy everything else. -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
