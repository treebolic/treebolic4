<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!--TREEBOLIC 2 TXTPAIR - 2015/02/01 (C) 2015 Author: Bernard Bou -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no" encoding="UTF-8" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<xsl:apply-templates select="./treebolic" />
	</xsl:template>

	<xsl:template match="treebolic">
		<xsl:text>#settings=&#xA;</xsl:text>
		<xsl:text>#parentid\tid\tlabel\tbackground\tforeground\timg\tlink\tcontent&#xA;</xsl:text>
		<xsl:apply-templates select="./tree" />
	</xsl:template>

	<xsl:template match="tree">
		<xsl:apply-templates select="./nodes" />
	</xsl:template>

	<xsl:template match="nodes">
		<xsl:apply-templates select=".//node" />
	</xsl:template>

	<xsl:template match="node">
		<xsl:value-of select="parent::node/@id" />
		<xsl:text>	</xsl:text>
		<xsl:value-of select="./@id" />
		<xsl:text>	</xsl:text>
		<xsl:apply-templates select="./label/text()" />
		<xsl:text>	</xsl:text>
		<xsl:value-of select="./@backcolor" />
		<xsl:text>	</xsl:text>
		<xsl:value-of select="./@forecolor" />
		<xsl:text>	</xsl:text>
		<xsl:value-of select="./img/@src" />
		<xsl:text>	</xsl:text>
		<xsl:value-of select="./a/@href" />
		<xsl:text>	</xsl:text>
		<xsl:apply-templates select="./content/text()" />
		<xsl:text>	</xsl:text>
		<xsl:text>&#xA;</xsl:text>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="@*">
		<xsl:attribute name="{name()}">
	  <xsl:value-of select="." />
	</xsl:attribute>
	</xsl:template>

</xsl:stylesheet>
