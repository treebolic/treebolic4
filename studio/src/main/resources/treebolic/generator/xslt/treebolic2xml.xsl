<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!-- TREEBOLIC 2 DATA - 09/04/2002 (C) 2002 Author: Bernard Bou -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="no" />

	<xsl:template match="/">
		<xsl:apply-templates select="./treebolic" />
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()" />
	</xsl:template>

	<xsl:template match="tree.descriptor">
		<xsl:apply-templates select="./tree" />
	</xsl:template>

	<xsl:template match="tree">
		<xsl:apply-templates select="./nodes" />
	</xsl:template>

	<xsl:template match="nodes">
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="label">
		<xsl:value-of select="translate(normalize-space(text()),' /','_-')" />
	</xsl:template>

	<xsl:template match="node">
		<xsl:variable name="nodename" select="translate(normalize-space(./label),' /','_-')" />
		<xsl:if test="$nodename=''">
			<xsl:call-template name="_node">
				<xsl:with-param name="nodename">
					anon
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not($nodename='')">
			<xsl:call-template name="_node">
				<xsl:with-param name="nodename" select="$nodename" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="_node">
		<xsl:param name="nodename" />
		<xsl:element name="{$nodename}">
			<!-- -->
			<xsl:apply-templates select="./@id" />
			<xsl:apply-templates select="./@forecolor" />
			<xsl:apply-templates select="./@backcolor" />
			<xsl:apply-templates select="./img" />
			<xsl:apply-templates select="./a" />
			<xsl:apply-templates select="./node" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="a">
		<xsl:element name="a">
			<xsl:attribute name="href">
	    <xsl:value-of select="@href" />
	  </xsl:attribute>
			<xsl:attribute name="target">
	    <xsl:value-of select="@target" />
	  </xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template match="img">
		<xsl:element name="img">
			<xsl:attribute name="src">
	    <xsl:value-of select="@src" />
	  </xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:attribute name="{name()}">
      <xsl:value-of select="." />
    </xsl:attribute>
	</xsl:template>

</xsl:stylesheet>
