<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!--TREEBOLIC 2 INDENTED TXT- 2008/07/08 (C) 2002 Author: Bernard Bou -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text" indent="no" encoding="UTF-8" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<xsl:apply-templates select="./treebolic" />
	</xsl:template>

	<xsl:template match="treebolic">
		<xsl:text>#settings=&#xA;</xsl:text>
		<xsl:text>#label:id:backcolor:forecolor:img:link:comment&#xA;</xsl:text>
		<xsl:apply-templates select="./tree" />
	</xsl:template>

	<xsl:template match="tree">
		<xsl:apply-templates select="./nodes" />
	</xsl:template>

	<xsl:template match="nodes">
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="node">
		<xsl:for-each select="ancestor::node">
			<xsl:text>  </xsl:text>
		</xsl:for-each>
		<xsl:apply-templates select="./label/text()" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="@backcolor" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="@forecolor" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="./img/@src" />
		<xsl:text>:</xsl:text>
		<xsl:variable name="uu" select="./a/@href" />
		<xsl:value-of select="$uu" />
		<!-- <xsl:value-of select="fn:replace($uu,':','####')"/> -->
		<xsl:text>:</xsl:text>
		<xsl:apply-templates select="./content/text()" />
		<xsl:text>&#xA;</xsl:text>

		<!-- recurse -->
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

</xsl:stylesheet>
