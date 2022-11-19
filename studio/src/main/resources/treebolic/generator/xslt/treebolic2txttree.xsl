<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!--TREEBOLIC 2 TXTTREE- 2008/07/08 (C) 2002 Author: Bernard Bou -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no" encoding="UTF-8" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<xsl:apply-templates select="./treebolic" />
	</xsl:template>

	<xsl:template match="treebolic">
		<xsl:text>#settings=&#xA;</xsl:text>
		<xsl:text>#label:ancestors:img:link:&#xA;</xsl:text>
		<xsl:apply-templates select="./tree" />
	</xsl:template>

	<xsl:template match="tree">
		<xsl:apply-templates select="./nodes" />
	</xsl:template>

	<xsl:template match="nodes">
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="node">
		<xsl:apply-templates select="./label" />
		<xsl:text>:</xsl:text>
		<xsl:text>/</xsl:text>
		<xsl:for-each select="ancestor::node">
			<xsl:value-of select="./label" />
			<xsl:text>/</xsl:text>
		</xsl:for-each>
		<xsl:text>:</xsl:text>
		<xsl:apply-templates select="./img" />
		<xsl:text>:</xsl:text>
		<xsl:apply-templates select="./a" />
		<xsl:text>&#xA;</xsl:text>
		<!-- recurse -->
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="label">
		<xsl:apply-templates select="text()" />
	</xsl:template>

	<xsl:template match="a">
		<xsl:apply-templates select="@href" />
	</xsl:template>

	<xsl:template match="img">
		<xsl:apply-templates select="@src" />
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

</xsl:stylesheet>
