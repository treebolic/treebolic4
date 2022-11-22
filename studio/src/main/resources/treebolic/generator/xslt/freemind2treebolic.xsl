<?xml version="1.0" encoding="UTF-8" ?>

<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!-- This code released under the GPL. (http://www.gnu.org/copyleft/gpl.html) Document : freemind2treebolic.xsl Created on : 01 May 2008, 17:17 Author : Bernard
	Bou 1313ou@gmail.com Description: transforms freemind mm format to treebolic ChangeLog: -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink"
	exclude-result-prefixes="xlink">
	<xsl:output method="xml" indent="yes" xmlns:xalan="http://xml.apache.org/xslt" xalan:indent-amount="4" encoding="UTF-8" omit-xml-declaration="no"
		doctype-system="Treebolic.dtd" />

	<xsl:template match="/">
		<xsl:element name="treebolic">
			<xsl:attribute name="toolbar">
			<xsl:text>true</xsl:text>
		</xsl:attribute>
			<xsl:attribute name="statusbar">
			<xsl:text>true</xsl:text>
		</xsl:attribute>
			<xsl:attribute name="popupmenu">
			<xsl:text>true</xsl:text>
		</xsl:attribute>
			<xsl:attribute name="tooltip">
			<xsl:text>true</xsl:text>
		</xsl:attribute>
			<xsl:element name="tree">
				<xsl:element name="nodes">
					<xsl:apply-templates select="//node" />
				</xsl:element>
				<xsl:element name="edges">
					<xsl:apply-templates select="//arrowlink" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tools">
				<xsl:element name="menu">
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:apply-templates select="//link" />
	</xsl:template>

	<xsl:template match="node">
		<xsl:element name="node">
			<xsl:attribute name="id">
			<xsl:choose>
				<xsl:when test="@ID != ''">
					<xsl:value-of select="@ID" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="generate-id(.)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
			<xsl:if test="./@COLOR != ''">
				<xsl:attribute name="forecolor">
				<xsl:value-of select="substring(./@COLOR,2)" />
			</xsl:attribute>
			</xsl:if>
			<xsl:if test="./@BACKGROUND_COLOR != ''">
				<xsl:attribute name="backcolor">
				<xsl:value-of select="substring(./@BACKGROUND_COLOR,2)" />
			</xsl:attribute>
			</xsl:if>
			<xsl:element name="label">
				<xsl:choose>
					<xsl:when test="string-length(@TEXT) > 10">
						<xsl:variable name="label1" select="substring(@TEXT,0,10)" />
						<xsl:variable name="label2" select="substring-before(substring(@TEXT,10),' ')" />
						<xsl:value-of select="concat($label1,$label2,'...')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@TEXT" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
			<xsl:element name="content">
				<xsl:value-of select="@TEXT" />
			</xsl:element>
			<xsl:if test="./edge">
				<xsl:element name="tree.edge">
					<xsl:choose>
						<xsl:when test="./edge/@WIDTH = 'thin'">
							<xsl:attribute name="stroke">
							<xsl:text>dot</xsl:text>
						</xsl:attribute>
						</xsl:when>
						<xsl:when test="./edge/@COLOR">
							<xsl:attribute name="color">
							<xsl:value-of select="substring(./edge/@COLOR,2)" />
						</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:element>
			</xsl:if>
			<xsl:if test="./icon">
				<xsl:element name="img">
					<xsl:attribute name="src">
					<xsl:value-of select="concat(./icon/@BUILTIN,'.gif')" />
				</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:if test="./@LINK != ''">
				<xsl:element name="a">
					<xsl:attribute name="href">
					<xsl:value-of select="./@LINK" />
				</xsl:attribute>
				</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="./node" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="arrowlink">
		<xsl:element name="edge">
			<xsl:attribute name="from">
			<xsl:choose>
				<xsl:when test="../@ID != ''">
					<xsl:value-of select="../@ID" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="generate-id(.)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
			<xsl:attribute name="to">
			<xsl:value-of select="@DESTINATION" />
		</xsl:attribute>
			<xsl:attribute name="toterminator">
			<xsl:text>a</xsl:text>
		</xsl:attribute>
			<xsl:if test="./@COLOR != ''">
				<xsl:attribute name="color">
				<xsl:value-of select="substring(./@COLOR,2)" />
			</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet> 
