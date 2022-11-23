<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!--TREEBOLIC 2 HTML 10/03/2008 (C) 2008 Author: Bernard Bou -->

<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="label"/>

	<xsl:template match="/">
		<xsl:apply-templates select="./treebolic"/>
	</xsl:template>

	<xsl:template match="treebolic">
		<xsl:apply-templates select="./tree"/>
	</xsl:template>

	<xsl:template match="tree">
		<xsl:apply-templates select="./nodes"/>
	</xsl:template>

	<xsl:template match="nodes">
		<UL>
			<xsl:apply-templates select="./node"/>
		</UL>
	</xsl:template>

	<xsl:template match="node">
		<LI>
			<DIV>
				<!--backcolor attribute specified. -->
				<xsl:if test="./@backcolor">
					<xsl:apply-templates select="./@backcolor"/>
				</xsl:if>
				<FONT FACE="sans-serif">
					<!--forecolor attribute specified. -->
					<xsl:if test="./@forecolor">
						<xsl:value-of select="./@forecolor"/>
					</xsl:if>
					<!--forecolor attribute not specified. -->
					<xsl:if test="not(./@forecolor)">
						<xsl:attribute name="COLOR">#0000ff</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates select="./label"/>
				</FONT>
			</DIV>
			<xsl:if test="./content">
				<xsl:text> content=</xsl:text>
				<xsl:apply-templates select="./content"/>
			</xsl:if>
			<xsl:if test="./img">
				<xsl:text> image=</xsl:text>
				<xsl:apply-templates select="./img"/>
			</xsl:if>
			<xsl:if test="./a">
				<xsl:text> link=</xsl:text>
				<xsl:apply-templates select="./a"/>
			</xsl:if>
		</LI>
		<xsl:if test="./node">
			<UL>
				<xsl:apply-templates select="./node"/>
			</UL>
		</xsl:if>
	</xsl:template>

	<xsl:template match="label">
		<xsl:apply-templates select="text()"/>
	</xsl:template>

	<xsl:template match="a">
		<xsl:call-template name="_a">
			<xsl:with-param name="dest" select="@href"/>
			<xsl:with-param name="frame" select="@target"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="_a">
		<xsl:param name="dest"/>
		<xsl:param name="frame"/>
		<xsl:element name="{name()}">
			<xsl:attribute name="href">
				<xsl:value-of select="$dest"/>
			</xsl:attribute>
			<xsl:if test="$frame">
				<xsl:attribute name="target">
					<xsl:value-of select="$frame"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="$dest"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="img">
		<xsl:call-template name="_img">
			<xsl:with-param name="src" select="@src"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="_img">
		<xsl:param name="src"/>
		<xsl:value-of select="$src"/>
	</xsl:template>

	<xsl:template match="@backcolor">
		<xsl:attribute name="STYLE">
			<xsl:value-of select="concat('background: #',.)"/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="@forecolor">
		<xsl:attribute name="COLOR">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:attribute name="{name()}">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()"/>
	</xsl:template>

</xsl:transform>
